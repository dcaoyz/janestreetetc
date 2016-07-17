package etcjane;

import java.lang.*;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AviatoConn {
	
	private static Socket s;
	
	public static void main(String[]args) {
		
		String reply;
		Date date = new Date();
		 try
	        {
	            s = new Socket("production", 20000);
	            BufferedReader from_exchange = new BufferedReader(new InputStreamReader(s.getInputStream()));
	            PrintWriter to_exchange = new PrintWriter(s.getOutputStream(), true);

	            to_exchange.println("HELLO AVIATO");
	            to_exchange.println("ADD 2 BOND BUY 998 5");
	            to_exchange.println("ADD 3 BOND BUY 997 5");
	            to_exchange.println("ADD 4 BOND BUY 995 5");
	            to_exchange.println("ADD 5 BOND BUY 990 5");

	            int id = 6;

	            int low = 0;
	            int high = 0;
	            
            	boolean buySet = false;
            	boolean init = false;
	         

	            while((reply = from_exchange.readLine()) != null) {
		            reply = reply.trim();
//		            SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss:SSSSSS");
//		            String formattedDate = sdf.format(date);
		            long tmp = System.nanoTime();
//		            if (reply.startsWith("BOOK")) {
//		            	String[] splits = reply.split("BUY|SELL");
//		            	int i=0;
//		            	for (String str : splits) {
//		            		if (i == 1){
//		            			System.out.print(tmp + ": BUY ");
//		 
//		            		}
//		            		else if (i == 2) {
//		            			System.out.print(tmp + ": SELL ");
//		            		}
//		            		System.out.printf("%s\n", str);
//		            		i++;
//		            	}
//		            } else {
		            	System.out.printf("%o: %s\n", tmp, reply);
//		            }
		            System.out.println();
								if (reply.startsWith("BOOK VALBZ")) {
									to_exchange.printf("OUTPUT");
									String[] response = reply.split(" ");

									for (int i = 0; i < response.length; i++) {
										if (response[i].equals("BUY")) {
											if (!(response[i+1]).equals("SELL")) {
												String buyValue = response[i+1];
												low = Integer.parseInt(buyValue.substring(0, buyValue.indexOf(":")));
												buySet = true;
											}
										}
										if (response[i].equals("SELL")) {
											if (i+1 != response.length) {
												String sellValue = response[i+1];
												high = Integer.parseInt(sellValue.substring(0, sellValue.indexOf(":")));
												if (buySet) init = true;
											}
									
										}
									}
								}

								if (reply.startsWith("BOOK VALE") && init) {
									double realValue = (low + high) / (2.0);

									String[] response = reply.split(" ");
									int buy, sell;

									for (int i = 0; i < response.length; i++) {
										if (response[i].equals("BUY")) {
											if (!(response[i+1]).equals("SELL")) {
												String buyValue = response[i+1];
												buy = Integer.parseInt(buyValue.substring(0, buyValue.indexOf(":")));
												if (buy + 1 < realValue) {
													to_exchange.printf("ADD %d VALE BUY %d 1\n", id++, buy + 1);
												}
											}
										}
										if (response[i].equals("SELL")) {
											if (i+1 != response.length) {
											String sellValue = response[i+1];
											sell = Integer.parseInt(sellValue.substring(0, sellValue.indexOf(":")));
								      if (sell - 1 > realValue) {
								      	to_exchange.printf("ADD %d VALE SELL %d 1\n", id++, sell - 1);
								      }
										}
										}
									}
								}

	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace(System.out);
	        } finally {
	        	try {
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	}
}
