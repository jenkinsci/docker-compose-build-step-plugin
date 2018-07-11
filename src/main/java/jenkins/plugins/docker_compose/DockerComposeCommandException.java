package jenkins.plugins.docker_compose;

/**
 * DockerComposeCommandException
 * 
 * @author <a href="mailto:jgalego1990@gmail.com">João Galego</a>
 */
public class DockerComposeCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public DockerComposeCommandException() {
        
		// Do nothing
    }

    public DockerComposeCommandException(String message) {
        
    	super(message);
    }

    public DockerComposeCommandException(Throwable ex) {
        
    	super(ex);
    }
}
