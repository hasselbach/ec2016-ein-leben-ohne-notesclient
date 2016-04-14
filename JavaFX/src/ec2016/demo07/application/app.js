angular.module('ec2016App', [])
  .controller('PizzaCtrl', function($scope){
    $scope.pizzas = [];
    
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