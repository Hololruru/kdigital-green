package com.springexample.springioc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class MyServiceConsumer implements ServiceConsumer {

	private MessageService messageService = new MyMessageService()
	
	public MyServiceConsumer() {}
	public MyServiceConsumer(MessageService messageService) {
		this.messageService = messageService;
	}			

	public MessageService getMessageService() {
		return messageService;
	}
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
	
	//////////////////////////////////////////
	
	private TimeService timeService = new MyTimeService();
	public void setTimeService(TimeService timeService) {
		this.timeService = timeService;
	}

	public void doSomething() {
		String message = messageService.getMessage();
		System.out.println(message);
		message = timeService.getTimeString();
		System.out.println(message);
	}

}
