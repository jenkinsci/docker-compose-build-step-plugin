package jenkins.plugins.docker_compose;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import java.io.Serializable;

/**
 * DockerComposeCommandOption
 * 
 * @author <a href="mailto:jgalego1990@gmail.com">João Galego</a>
 */
public abstract class DockerComposeCommandOption implements Describable<DockerComposeCommandOption>, Serializable {
	
	private static final long serialVersionUID = 1L;

	public abstract int execute(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener, DockerComposeBuilder dockerComposeStep)
            throws DockerComposeCommandException;

    /* (non-Javadoc)
     * @see hudson.model.Describable#getDescriptor()
     */
    @Override
    @SuppressWarnings({ "unchecked" })
    public Descriptor<DockerComposeCommandOption> getDescriptor() {
        
    	try {
            return Jenkins.getInstance().getDescriptorOrDie(getClass());
        } catch (NullPointerException ex) {
            throw ex;
        }
    }
}
