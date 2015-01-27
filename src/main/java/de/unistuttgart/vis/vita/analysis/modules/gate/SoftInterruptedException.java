package de.unistuttgart.vis.vita.analysis.modules.gate;

/**
 * A runtime exception with the semantics of an {@link InterruptedException}
 */
public class SoftInterruptedException extends RuntimeException {
	public SoftInterruptedException() {
	}

	public SoftInterruptedException(String message) {
		super(message);
	}

	public SoftInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public SoftInterruptedException(Throwable cause) {
		super(cause);
	}
}
