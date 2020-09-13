import java.net.*;

public final class WebServer
{
    public static void main(String argv[]) throws Exception
    {
        // Ajustar do número da porta
        int port = 6789;

        // Estabelecer o socket de escuta
        ServerSocket initialSocket = new ServerSocket(port); 

        // Processar a requisição de serviço HTTP em um laço infinito
        while(true) { 
            Socket connectionSocket = initialSocket.accept(); 
            
            // Construir um objeto para processamento da mensagem de requisição HTTP
            HttpRequest request = new HttpRequest(connectionSocket);

            // Criação de um novo thread para processar a requisição.
            Thread thread = new Thread(request);
            // Execução do thread.
            thread.start();
            
        }
    }
}
