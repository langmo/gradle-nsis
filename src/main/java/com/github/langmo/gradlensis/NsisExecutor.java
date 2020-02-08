package com.github.langmo.gradlensis;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.Project;
import org.gradle.api.Action;
import org.gradle.api.GradleScriptException;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RelativePath;
import org.gradle.api.logging.LogLevel;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

class NsisExecutor 
{
	private static final String SOURCE_NSIS_FOLDER = "nsis-3.0.5";
	private static final String SOURCE_PLUGINS_FOLDER = "plugins";
	private static final String DEST_PLUGINS_FOLDER = "Plugins";
	private static final String DEST_PLUGINS_ANSI_FOLDER = "x86-ansi";
	@SuppressWarnings("unused")
	private static final String DEST_PLUGINS_UNICODE_FOLDER = "x86-unicode";
	static RegularFile execute(final Project project, final Directory extractTo, final Directory runIn, final Map<String, String> variables, final Set<String> additionalPlugins, final RegularFile configuration) throws GradleScriptException
	{
		final RegularFile makensisPath = extract(project, extractTo);
		for(String additionalPlugin : additionalPlugins)
		{
			extractPlugin(project, additionalPlugin, extractTo);
		}
		final ByteArrayOutputStream logger = new ByteArrayOutputStream()
		{
			@Override
			public void write(int b)
			{
				System.out.write(b);
				super.write(b);
			}
			@Override
			public void write(byte[] b, int off, int len)
			{
				System.out.write(b, off, len);
				super.write(b, off, len);
			}
			@Override
			public void write(byte[] b) throws IOException
			{
				System.out.write(b);
				super.write(b);
			}
		};
		ExecResult result = project.exec(new Action<ExecSpec>() 
		{
			@Override
			public void execute(ExecSpec spec) {
				spec.setExecutable(makensisPath);
				spec.setWorkingDir(runIn);
				// prevent NSIS from automatically changing current path to NSIS configuration file
				spec.args("/NOCD");
				for(Map.Entry<String, String> entry : variables.entrySet())
				{
					spec.args("/D"+entry.getKey()+"="+entry.getValue());
				}
				spec.args(configuration.toString());
				spec.setStandardOutput(logger);
			}
		});
		result.assertNormalExitValue();
		// Find part in NSIS console output where it is stated how the generated installer exe file is named
		Pattern installerFilePattern = Pattern.compile("Output: \"([^\"$]{4,})\"");
		Matcher matcher = installerFilePattern.matcher(logger.toString());
		if(!matcher.find())
			throw new GradleScriptException("Couldn't find out where installer was created by NSIS.", null);
		String installerPath = matcher.group(1);
		String relative = project.getProjectDir().toURI().relativize(new File(installerPath).toURI()).getPath();
		return project.getLayout().getProjectDirectory().file(relative);
	}
	private static RegularFile extract(final Project project, final Directory extractTo) throws GradleScriptException
	{
		RegularFile makensisPath = extractTo.file("makensis.exe");
		if(makensisPath.getAsFile().exists())
		{
			project.getLogger().log(LogLevel.INFO, "NSIS already extracted to: " + extractTo);
			return makensisPath;
		}
		project.getLogger().log(LogLevel.INFO, "Extracting NSIS to: " + extractTo);
		File pathToJar;
		try 
		{
			pathToJar = new File(NsisExecutor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} 
		catch (URISyntaxException e) {
			throw new GradleScriptException("Could not find out path to JAR of plugin.", e);
		}
		final FileTree jarContent = project.zipTree(pathToJar);
		project.copy(new Action<CopySpec>(){

			@Override
			public void execute(CopySpec spec) 
			{
				spec.from(jarContent);
				spec.setIncludeEmptyDirs(false);
				spec.into(extractTo);
				spec.eachFile(new Action<FileCopyDetails>() {

					@Override
					public void execute(FileCopyDetails fcd) {
						if(fcd.getRelativePath().getPathString().startsWith(SOURCE_NSIS_FOLDER))
						{
							String[] pathElems = fcd.getRelativePath().getSegments();
							// Delete first element: That's "bin", we already know that
							pathElems = Arrays.copyOfRange(pathElems, 1, pathElems.length);
							fcd.setRelativePath(new RelativePath(!fcd.getFile().isDirectory(), pathElems));
			                fcd.setMode(0755);
						}
						else
							fcd.exclude();
					}
					
				});
			}
		});
		if(!makensisPath.getAsFile().exists())
			throw new GradleScriptException("Extracted NSIS files to "+extractTo.toString()+", but file "+makensisPath.toString()+" cannot be found.", null);
		return makensisPath;
	}
	private static void extractPlugin(final Project project, final String pluginName, final Directory extractTo) throws GradleScriptException
	{
		RegularFile pluginDllPath = extractTo.file(DEST_PLUGINS_FOLDER + "/" + DEST_PLUGINS_ANSI_FOLDER+"/"+pluginName+".dll");
		if(pluginDllPath.getAsFile().exists())
		{
			project.getLogger().log(LogLevel.INFO, "Plugin "+pluginName+" already installed");
			return;
		}
		project.getLogger().log(LogLevel.INFO, "Extracting Plugin " + pluginName);
		File pathToJar;
		try 
		{
			pathToJar = new File(NsisExecutor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} 
		catch (URISyntaxException e) {
			throw new GradleScriptException("Could not find out path to JAR of plugin.", e);
		}
		final Directory dest = extractTo.dir(DEST_PLUGINS_FOLDER);
		final FileTree jarContent = project.zipTree(pathToJar);
		class FoundPluginIndicator
		{
			boolean found = false;
		}
		final FoundPluginIndicator foundPlugin = new FoundPluginIndicator();
		project.copy(new Action<CopySpec>(){

			@Override
			public void execute(CopySpec spec) 
			{
				spec.from(jarContent);
				spec.setIncludeEmptyDirs(false);
				spec.into(dest);
				spec.eachFile(new Action<FileCopyDetails>() {

					@Override
					public void execute(FileCopyDetails fcd) {
						if(fcd.getRelativePath().getPathString().startsWith(SOURCE_PLUGINS_FOLDER+"/"+pluginName))
						{
							String[] pathElems = fcd.getRelativePath().getSegments();
							// Delete first element: That's "bin", we already know that
							pathElems = Arrays.copyOfRange(pathElems, 2, pathElems.length);
							fcd.setRelativePath(new RelativePath(!fcd.getFile().isDirectory(), pathElems));
			                fcd.setMode(0755);
			                foundPlugin.found = true;
						}
						else
							fcd.exclude();
					}
					
				});
			}
		});
		if(!foundPlugin.found)
			throw new GradleScriptException("Could not find plugin "+pluginName + ". Please check spelling.", null);
	}
}
