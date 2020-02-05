package io.github.langmo.gradlensis;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.gradle.api.Project;
import org.gradle.api.Action;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RelativePath;

class NsisExtractor 
{
	static void extract(final Project project, final Directory target) throws Exception
	{
		final File pathToJar = new File(NsisExtractor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		final FileTree jarContent = project.zipTree(pathToJar);
		project.copy(new Action<CopySpec>(){

			@Override
			public void execute(CopySpec spec) 
			{
				spec.from(jarContent);
				spec.setIncludeEmptyDirs(false);
				spec.into(target);
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
				/*from { project.zipTree(workingJar) }
                includeEmptyDirs = false
                into { destination }
                eachFile { FileCopyDetails fcp ->
                    // only extract the binaries
                    if (fcp.relativePath.pathString.startsWith(jarName)) {
                        // remap the file to the root
                        def segments = fcp.relativePath.segments
                        def pathSegments = segments[1..-1] as String[]
                        fcp.relativePath = new RelativePath(!fcp.file.isDirectory(), pathSegments)
                        fcp.mode = 0755
                    } else {
                        fcp.exclude()
                    }
                }*/
			}});
	}
}
