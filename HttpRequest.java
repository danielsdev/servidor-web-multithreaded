import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class HttpRequest implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;

    // Construtor 
    public HttpRequest(Socket socket) throws Exception
    {
        this.socket = socket;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Envio do arquivo solicitado
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        // Construir um buffer de 1K para comportar os bytes no caminho para o socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copiar o arquivo requisitado dentro da cadeia de saída do socket
        while((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            return "image/jpeg";
        }

        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }

        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        }

        return "application/octet-stream";
    }

    private void processRequest() throws Exception
    {
        InputStream is = this.socket.getInputStream();
        DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // Extrair o nome do arquivo a linha de requisição
        StringTokenizer tokens = new StringTokenizer(br.readLine());
        tokens.nextToken();

        String fileName = tokens.nextToken();
        fileName = "." + fileName;

        if (fileName.equals("./"))
            fileName += "index.html";

        String httpVersion = tokens.nextToken();

        FileInputStream fis = null;
        Boolean fileExists = true;

        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if (fileExists) {
            statusLine = httpVersion + " 200" + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        } else {
            statusLine = httpVersion + " 404" + CRLF;
            contentTypeLine = "Content-type: " + contentType(".html") + CRLF;
            entityBody =   "<HTML>"
                        + "<HEAD><TITLE>Not Found</TITLE></HEAD>" 
                        + "<BODY>Not Found</BODY>"
                        + "</HTML>";
        }

        // Enviar a linha de status.
        os.writeBytes(statusLine);
        // Enviar a linha de tipo de conteúdo.
        os.writeBytes(contentTypeLine);
        // Enviar uma linha em branco para indicar o fim das linhas de cabeçalho.      
        os.writeBytes(CRLF);

        if(fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }

        //String headerLine = null;

        // while ((headerLine = br.readLine()).length() != 0) {
        //     System.out.println((headerLine));
        // }

        //handleResponse(fileName, "", httpVersion, os);

        os.close();
        br.close();
        socket.close();  
    }
}
