package jenkins.plugins.docker_compose;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

/**
 * Docker Compose Builder
 * 
 * @author <a href="mailto:jgalego1990@gmail.com">João Galego</a>
 */
public class DockerComposeBuilder extends Builder implements SimpleBuildStep {

	// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(DockerComposeBuilder.class);
	
	// Constants
	private static final String DISPLAY_NAME = "Docker Compose Build Step";
	private static final String EMPTY_FIELD_MESSAGE = "This field should not be empty";

	// Form parameters
	public final boolean useCustomDockerComposeFile;
	public final String dockerComposeFile;
	private DockerComposeCommandOption option;

	@DataBoundConstructor
	public DockerComposeBuilder(boolean useCustomDockerComposeFile, String dockerComposeFile, DockerComposeCommandOption option) {
		
		this.useCustomDockerComposeFile = useCustomDockerComposeFile;
		this.dockerComposeFile = dockerComposeFile;
		this.option = option;
	}

	public boolean getUseCustomDockerComposeFile() {
		
		return useCustomDockerComposeFile;
	}

	public String getDockerComposeFile() {
		
		return dockerComposeFile;
	}

	public DockerComposeCommandOption getOption() {
		
		return option;
	}

	@Override
	public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException {
		
		try{
			LOGGER.info("Executing Docker Compose build step");
			int exitCode = option.execute(build, workspace, launcher, listener, this);

			LOGGER.info("Exit code: {}", exitCode);
			if (exitCode == 0) {
				build.setResult(Result.SUCCESS);
			}
			else {
				build.setResult(Result.FAILURE);
			}
		} catch (DockerComposeCommandException ex) {
			LOGGER.error("Docker Compose build step failed to execute", ex);
			ex.printStackTrace();
			build.setResult(Result.FAILURE);
		}
	}

	@Override
	public DescriptorImpl getDescriptor() {

		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

		public DescriptorImpl() {
			load();
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			
			// Indicates that this builder can be used with all kinds of project types
			return true;
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {

			// Can also use req.bindJSON(this, formData)
			save();

			return super.configure(req, formData);
		}

		@Override
		public String getDisplayName() {

			return DISPLAY_NAME;
		}

		public static DescriptorExtensionList<DockerComposeCommandOption, DockerComposeCommandOptionDescriptor> getOptionList() {

			return DockerComposeCommandOptionDescriptor.all();
		}
		
		public FormValidation doCheckDockerComposeFile(@QueryParameter String value) {

            if(value.isEmpty()) {
                return FormValidation.error(EMPTY_FIELD_MESSAGE);
            }

            return FormValidation.ok();
        }
	}
}
