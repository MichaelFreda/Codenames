package HW4;



import java.io.*;
import java.net.*;

class CodenamesClient {
	
	final static String CRLF = "\r\n";

	public static void main(String argv[]) throws Exception {
		
		Socket clientSocket = new Socket("127.0.0.1", 6789);
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		
		while (true) {
			
			String serverMessage = inFromServer.readLine();
			
				if (serverMessage != null) {
					System.out.println(serverMessage);
				}

				if (serverMessage.equals("Please provide a hint for your team.")){
					String hint = inFromUser.readLine();
					outToServer.writeBytes(hint + CRLF);
				}

				if(serverMessage.equals("Please provide the number of words your hint correlates with.")) {
					String num = inFromUser.readLine();
					outToServer.writeBytes(num + CRLF);
				}

				if(serverMessage.equals("Please guess a word for your team")) {
					String guess = inFromUser.readLine();
					outToServer.writeBytes(guess + CRLF);
				}
			}
		
	}
}





