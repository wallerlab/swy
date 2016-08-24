package org.wallerlab.swy.service.utils

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.TimeUnit

/**
 * This is a simple parellalization scheme.
 * 
 *  TODO this class is not set up nicely (see warning; it is not ensured that the callable class
 *    is callable and HAS a returnValue of type ReturnClass and so on...
 */
@Service
@Profile(["unstable"])
class ParallelTask<CallableClass, ReturnClass> {
	
	private int tasksWorking
	
	private int poolSize
	private ExecutorService threadPool
	private ExecutorCompletionService<ReturnClass> taskCompletionService
	
	private CallableClass[] arrayOfCallableObjects
	
	ParallelTask(int poolSize) {
		threadPool = Executors.newFixedThreadPool(poolSize);
		taskCompletionService = new ExecutorCompletionService<ReturnClass>(threadPool);
		this.poolSize = poolSize
	}
	
	ReturnClass getTaskResult() {
		if (tasksWorking > 0) {
			ReturnClass returnObject = taskCompletionService.take().get()
			tasksWorking--
			if (arrayOfCallableObjects) {
				runTask(arrayOfCallableObjects[0])
			}
			return returnObject
		} else {
			return null
		}
	}
	
	@SuppressWarnings("unchecked")
	void runTasks(CallableClass[] arrayOfCallableObjects) {
		this.arrayOfCallableObjects = arrayOfCallableObjects
		tasksWorking = 0;
		for(Callable callableObject in arrayOfCallableObjects){
			while (tasksWorking < poolSize) {
				runTask(callableObject)
			}
		}
	}

	void runTask(Callable callableObject) {
		taskCompletionService.submit(callableObject)
		tasksWorking++
		arrayOfCallableObjects -= callableObject
	}
	
	void cleanUp(){
		threadPool.shutdownNow()
		threadPool.awaitTermination(10L, TimeUnit.SECONDS)
	}
}
