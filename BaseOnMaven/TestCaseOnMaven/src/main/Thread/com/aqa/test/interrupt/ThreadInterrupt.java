package com.aqa.test.interrupt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * description: 
 * <p>阻塞(占用CPU)线程无法被中断，而睡眠（让出CPU）的线程可以被中断 </p>
 * 2017年9月19日 上午10:48:14
 * @author HCL
 */
public class ThreadInterrupt {

	static Thread threadA = new Thread(() -> {
		System.out.println("-----thread A start-----");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("-----thread A had been interrupted-----");
			e.printStackTrace();
		}
	});

	static Thread threadB = new Thread(() -> {
		System.out.println("-----thread B start-----");
		try {
			RandomAccessFile file = new RandomAccessFile("E:\\testInterrupt.txt", "rw");
			while(true){
				file.writeChars("China");
				file.writeUTF("China");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	});

	public static void main(String[] args) {
		
		try {
			threadA.start();
			threadB.start();
			threadB.interrupt();
			threadA.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		
	}

}
