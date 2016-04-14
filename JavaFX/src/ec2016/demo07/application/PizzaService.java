package ec2016.demo07.application;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

public interface PizzaService {

	@GET("api/pizzas")
	Call<List<Pizza>> listPizzas();
}
