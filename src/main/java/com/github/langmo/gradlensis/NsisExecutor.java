package com.github.langmo.gradlensis;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.gradle.api.Project;
import org.gradle.api.Action;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RelativePath;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

class NsisExecutor 
{
	static RegularFile execute(final Project project, final Directory extractTo, final Directory runIn, final RegularFile configuration) throws Exception
	{
		final RegularFile makensisPath = extract(project, extractTo);
		ExecResult result = project.exec(new Action<ExecSpec>() {

			@Override
			public void execute(ExecSpec spec) {
				spec.setExecutable(makensisPath);
				spec.setWorkingDir(runIn);
				spec.args("/NOCD", configuration.toString());
				System.out.println("Running " + spec.getCommandLine());
			}
		});
		result.assertNormalExitValue();
		// TODO: Which file is the installer?
		return configuration;
	}
	private static RegularFile extract(final Project project, final Directory extractTo) throws Exception
	{
		RegularFile makensisPath = extractTo.file("makensis.exe");
		if(makensisPath.getAsFile().exists())
			return makensisPath;
		final File pathToJar = new File(NsisExecutor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
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
						if(fcd.getRelativePath().getPathString().startsWith("bin"))
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
			throw new Exception("Extracted NSIS files to "+extractTo.toString()+", but file "+makensisPath.toString()+" cannot be found.");
		return makensisPath;
	}
}
