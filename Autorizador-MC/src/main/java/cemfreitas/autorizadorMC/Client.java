package cemfreitas.autorizadorMC;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cemfreitas.autorizadorMC.utils.AppFunctions;

/* Client Class.
 *  Used to provide client socket services. 
 */
public class Client {
	private int clientPort;
	private String clienteIP, clientName;
	private Socket clienteSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private byte[] requestTransaction, responseTransaction;

	//Constructor receives connection data.
	public Client(String clientIP, int clientPort, String clientName) {
		this.clienteIP = clientIP;
		this.clientPort = clientPort;
		this.clientName = clientName;
	}

	//Send a message to host
	public void sendTransaction() throws IOException {
		if (outputStream != null) {
			try {
				outputStream.write(requestTransaction);
			} catch (IOException e) {
				throw new IOException("Erro ao enviar menssagem para " + clientName + ": " + e.getMessage());
			}

		} else {
			throw new IOException("Conexao com a " + clientName + " perdida");
		}
	}

	public byte[] getResponseTransaction() {
		return responseTransaction;
	}

	public void setRequestTransaction(byte[] requestTransaction) {
		this.requestTransaction = requestTransaction;
	}

	//Try to connect to host.
	public void clientConnect() throws IOException  {		
		try {
			clienteSocket = new Socket(clienteIP, clientPort);
			inputStream = clienteSocket.getInputStream();
			outputStream = clienteSocket.getOutputStream();
		} catch (IOException e) {
			throw new IOException("Erro ao tentar conectar " + clientName + ": " + e.getMessage());
		}
		
	}

	//Used to test a connection.
	public boolean testConnection() throws IOException {
		try {
			clientConnect();
		} catch (IOException e) {
			return false;
		} finally {
			closeConnection();
		}
		return true;
	}

	public void closeConnection() throws IOException {
		try {
			if (clienteSocket != null)
				clienteSocket.close();
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();

			clienteSocket = null;
			inputStream = null;
			outputStream = null;
		} catch (IOException e) {
			throw new IOException("Erro ao fechar conexao com " + clientName + ": " + e.getMessage());
		}
	}

	//Receive a message from host.
	public void receiveTransaction() throws IOException {
		byte[] msg = null;
		byte pbyte = 0;
		try {
			pbyte = (byte) inputStream.read();
			msg = new byte[inputStream.available()];
			inputStream.read(msg);
			if (msg.length == 0) {
				return;
			}
		} catch (IOException e) {
			throw new IOException("Erro ao receber transacao do(a) " + clientName + ": " + e.getMessage());
		}

		responseTransaction = AppFunctions.concatenate(pbyte, msg);

		if (responseTransaction == null) {
			throw new IOException(clientName + " retornou nulo");			
		}
	}
}
