package ec2016.demo09.application;


import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class RESTClient {
	Retrofit retrofit ;
    PizzaService service;
    
	public RESTClient(){
		// REST Client
        retrofit = new Retrofit.Builder()
	    		.baseUrl("http://localhost:8080/pizzapizza.nsf/xsp/")
	    		.addConverterFactory(GsonConverterFactory.create())
	    		.build();
        
        // init des Pizza Service
        service = retrofit.create(PizzaService.class);
        
	}
}
