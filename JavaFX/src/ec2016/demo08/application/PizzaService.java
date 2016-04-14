package ec2016.demo08.application;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface PizzaService {

	@GET("api/pizzas")
	Call<List<Pizza>> listPizzas();
	
	@POST("api/pizza/new")
	Call<Pizza> createPizza(@Body Pizza pizza);
}
