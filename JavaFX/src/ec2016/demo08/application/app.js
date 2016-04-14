angular.module('ec2016App', [])
  .controller('PizzaCtrl', function($scope){
    $scope.pizzas = [];
    
    $scope.addPizza = function(paramId, paramName, paramPrice) {
    	$scope.pizzas.push( { id: paramId, name: paramName, price: paramPrice });
    };
    
    $scope.submit = function(){
    	var id = $scope.newpizza.id;
    	var name = $scope.newpizza.name;
    	var price = $scope.newpizza.price;

    	$scope.addPizza(  id, name, price );
  	  	app.savePizza( id, name, price );
    }
});

function addPizzaToScope(id, name, price) {
    var scope = angular.element(
    document.getElementById("Pizzas")).scope();
    scope.$apply(function () {
        scope.addPizza(id, name, price);
    });
}