package org.mokai;

import java.util.List;

import org.mokai.spi.Acceptor;
import org.mokai.spi.Action;
import org.mokai.spi.Processor;

/**
 * 
 * 
 * @author German Escobar
 */
public interface ProcessorService extends Service {

	String getId();

	int getPriority();
	
	Processor getProcessor();
	
	boolean isServiceable();
	
	ProcessorService addAcceptor(Acceptor acceptor) throws IllegalArgumentException, ObjectAlreadyExistsException;
	
	ProcessorService removeAcceptor(Acceptor acceptor) throws IllegalArgumentException, ObjectNotFoundException;

	List<Acceptor> getAcceptors();
	
	ProcessorService addPreProcessingAction(Action action) throws IllegalArgumentException, ObjectAlreadyExistsException;
	
	ProcessorService removePreProcessingAction(Action action) throws IllegalArgumentException, ObjectNotFoundException;
	
	List<Action> getPreProcessingActions();
	
	ProcessorService addPostProcessingAction(Action action) throws IllegalArgumentException, ObjectAlreadyExistsException;
	
	ProcessorService removePostProcessingAction(Action action) throws IllegalArgumentException, ObjectNotFoundException;
	
	List<Action> getPostProcessingActions();
	
	ProcessorService addPostReceivingAction(Action action) throws IllegalArgumentException, ObjectAlreadyExistsException;
	
	ProcessorService removePostReceivingAction(Action action) throws IllegalArgumentException, ObjectNotFoundException;
	
	List<Action> getPostReceivingActions();
	
	void destroy();
	
}
