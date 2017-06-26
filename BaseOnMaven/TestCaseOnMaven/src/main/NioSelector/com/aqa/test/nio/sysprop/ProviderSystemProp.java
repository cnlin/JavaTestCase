package com.aqa.test.nio.sysprop;

public class ProviderSystemProp {
	
	public static void main(String[] args) {
		
		String property = System.getProperty("java.nio.channels.spi.SelectorProvider");
		System.out.println(" System.getProperty(\"java.nio.channels.spi.SelectorProvider\")"+property);
		
	}

}
