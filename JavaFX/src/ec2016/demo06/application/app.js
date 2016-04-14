angular.module('ec2016App', [])
  .controller('PizzaCtrl', function($scope){
    $scope.pizzas = [
      { id: 1, name: "Pizza Vegetaria", price: 5 },
      { id: 2, name: "Pizza Salami",    price: 5.5 },
      { id: 3, name: "Pizza Thunfisch", price: 6 }
    ];
    
    $scope.addPizza = function(paramId, paramName, paramPrice) {
    	$scope.pizzas.push( { id: paramId, name: paramName, price: paramPrice });
    };
  
});

function addPizzaToScope(id, name, price) {
    var scope = angular.element(
    document.getElementById("Pizzas")).scope();
    scope.$apply(function () {
        scope.addPizza(id, name, price);
    });
}