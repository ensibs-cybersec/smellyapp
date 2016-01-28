angular.module('mortgageSampleApp', []);

angular.module('mortgageSampleApp')
  .factory('Mortgages', ['$http', function($http) {
    return {
      get: function() {
        return $http.get('/api/mortgages');
      },
      getPersonnes: function() {
        return $http.get('/api/personnes').then(function(response) { return response.data; });
      },
      report: function(reportData) {
        return $http.post('/api/report', reportData);
      },
      create: function(mortgageData) {
        return $http.post('/api/mortgages', mortgageData);
      }
    };
  }]);

angular.module('mortgageSampleApp')
  .controller('mainController', ['$scope', 'Mortgages', function($scope, Mortgages) {

    //$scope.birthdate = new Date(2015, 1, 25);
    
    Mortgages.get()
      .success(function(data) {
        $scope.mortgages = data;
      });

    Mortgages.getPersonnes()
      .then(function(data) {
        console.log("getPersonnes RESULT");
        console.log(data);
        $scope.personnes = data;
        //$scope.birthdate = $filter('date')(new Date(),'yyyy-MM-dd');
	return;
      });

    /**
     * Creates the mortgage into persistance from the values in the view
     */
    $scope.createPersonne = function(personne) {
        Mortgages.create(personne)
          .then(function(data) {
            return Mortgages.getPersonnes();
          })
	  .then(function(personnes) {
	     $scope.personnes = personnes;
	     $scope.personne = {};
	  })
	  .catch(function(error) {
	     console.log(error);
	  });
    };

    // /**
    //  * Generates a report about best repayment strategy for a mortgage
    //  */
    // $scope.sendReport = function() {
    //   if (angular.isObject($scope.reportForm)) {
    //     Mortgages.report($scope.reportForm);
    //     $scope.feedbackMessage = "Your report will be sent quickly";
    //   }
    // };

  }]);

