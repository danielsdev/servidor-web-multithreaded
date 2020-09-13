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

    private void processRequest() throws Exception
    {
        InputStream is = this.socket.getInputStream();
        DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String headerLine = null;

        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println((headerLine));
        }

        os.close();
        br.close();
        socket.close();  
    }
}
