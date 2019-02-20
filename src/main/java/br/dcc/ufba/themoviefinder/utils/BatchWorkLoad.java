package br.dcc.ufba.themoviefinder.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.primes.Primes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.util.Callback;
import net.codecrafting.springfx.util.AsyncUtils;

public class BatchWorkLoad<T> 
{
	private int batchSize;
	private List<List<T>> workLoadList;
	private List<T> srcList;
	private static final Logger LOGGER = LogManager.getLogger(BatchWorkLoad.class);
	
	public BatchWorkLoad(int batchSize, List<T> srcList)
	{
		this(batchSize, srcList, true);
	}
	
	public BatchWorkLoad(int batchSize, List<T> srcList, boolean fitBatchSize)
	{
		this.srcList = srcList;
		this.batchSize = batchSize;
		if(fitBatchSize) {
			fitBatchSize();	
		}
		workLoadList = createWorkLoadList();
	}
	
	public int getBatchSize() 
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize) 
	{
		this.batchSize = batchSize;
	}

	public List<T> getSrcList() 
	{
		return srcList;
	}

	public void setSrcList(List<T> srcList) 
	{
		this.srcList = srcList;
	}
	
	public void run(Callback<T, Void> callback) throws Exception
	{
		for(List<T> batchList : workLoadList) {
			int batchListSize = batchList.size();
			CompletableFuture<Boolean> completable = new CompletableFuture<Boolean>();
			AtomicInteger batchCounter = new AtomicInteger(1);
			for (int i = 0; i < batchListSize; i++) {
				final T load = batchList.get(i);
				AsyncUtils.async(() -> {
					try {
						callback.call(load);	
					} catch(Exception e) {
						completable.completeExceptionally(e);
						LOGGER.error(e.getMessage(), e);
					}
					int v = batchCounter.getAndIncrement();
					if(v == batchListSize) {
						completable.complete(true);
					}
				});
			}
			completable.get();
		};
	}

	private List<List<T>> createWorkLoadList() 
	{
	    List<T> list = new ArrayList<T>(srcList);
	    int listSize = list.size();
	    if(listSize > 0) {
		    if (batchSize <= 0 || batchSize > listSize) {
		    	batchSize = listSize;	
		    }
		    int numPages = (int) Math.ceil((double) listSize / (double) batchSize);
		    List<List<T>> pages = new ArrayList<List<T>>(numPages);
		    for (int pageNum = 0; pageNum < numPages;) {
		        pages.add(list.subList(pageNum * batchSize, Math.min(++pageNum * batchSize, listSize)));	
		    }
		    return pages;	
	    }
	    return new ArrayList<List<T>>();
	}
	
	private void fitBatchSize()
	{
		int oSrcSize = srcList.size(), srcSize = srcList.size(), fitBatchSize = Math.min(batchSize, 4);
		if(srcSize > 0 && batchSize > 1) {
	        if(srcSize > batchSize && Primes.isPrime(srcSize)) {
	        	srcSize++;
	        }
	        for(int i = 1; i <= Math.min(srcSize, batchSize); ++i) {
	            if (srcSize % i == 0) {
	            	fitBatchSize = i;
	            }
	        }
	        fitBatchSize = Math.max(fitBatchSize, 4);
		}
        if(LOGGER.isTraceEnabled()) {
        	LOGGER.trace(String.format("oSrcSize: %d, srcSize: %d, batchSize: %d, bestFitBatchSize: %d", oSrcSize, srcSize, batchSize, fitBatchSize));
        }
		batchSize = fitBatchSize;
	}
	
}
