package pharmancyApp.rest;


public class Response {
    private final String response;
    private final int respondCode;

    public Response(String response,int respondCode){
        this.response =response;
        this.respondCode =respondCode;
    }

    public String getResponse() {
        return response;
    }

    public int getRespondCode() {
        return respondCode;
    }
}
