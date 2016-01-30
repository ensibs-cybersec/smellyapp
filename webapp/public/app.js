angular.module('smellyApp', []);

angular.module('smellyApp')
  .factory('Persons', ['$http', function($http) {
    return {
      getPersons: function() {
        return $http.get('/api/person').then(function(response) { return response.data; });
      },
      createPerson: function(person) {
        return $http.post('/api/person', person);
      },
      logout: function() {
        return $http.get('/logout');  
      }
    };
  }]);

angular.module('smellyApp')
  .controller('mainController', ['$scope', 'Persons', function($scope, Factory) {

    Factory.getPersons()
      .then(function(data) {
        $scope.persons = data;
	    return;
      });

    $scope.createPerson = function(person) {
        Factory.createPerson(person)
          .then(function(data) {
            return Factory.getPersons();
          })
	  .then(function(persons) {
	     $scope.persons = persons;
	     $scope.person = {};
	  })
	  .catch(function(error) {
	     console.log(error);
	  });
    };

    $scope.logout = function() {
        Factory.logout();
    };
  }]);

