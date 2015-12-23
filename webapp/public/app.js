angular.module('mortgageSampleApp', []);

angular.module('mortgageSampleApp')
  .factory('Mortgages', ['$http', function($http) {
    return {
      get: function() {
        return $http.get('/api/mortgages');
      },
      getPersonnes: function() {
        return $http.get('/api/personnes');
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
  .controller('mainController', ['$scope', '$http', 'Mortgages', function($scope, $http, Mortgages) {

    //$scope.birthdate = new Date(2015, 1, 25);
    
    Mortgages.get()
      .success(function(data) {
        $scope.mortgages = data;
      });

    Mortgages.getPersonnes()
      .success(function(data) {
        console.log("getPersonnes RESULT");
        console.log(data);
        $scope.personnes = data;
        //$scope.birthdate = $filter('date')(new Date(),'yyyy-MM-dd');
      });

    /**
     * Creates the mortgage into persistance from the values in the view
     */
    $scope.createPersonne = function() {
      if (angular.isObject($scope.createForm)) {
        Mortgages.create($scope.createForm)
          .success(function(data) {
            console.log("succes 1")
            // $scope.reportForm = $scope.reportForm || {};
            // $scope.reportForm.reference = $scope.createForm.text;
            $scope.createForm = {};
            console.log("succes 2")
//            $scope.mortgages = data;
            console.log("createPersonne RESULT");
            console.log(data);
            $scope.personnes = data;
            console.log("succes 3")
// MARCHE PAS            Mortgages.getPersonnes().success(function(data) { $scope.personnes = data; });
          });
      }
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

