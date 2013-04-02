angular.module('codebrag.commits')

    .controller('CommitsCtrl', function ($location, $scope, $http, PendingCommits, currentCommit) {

        $scope.currentCommit = currentCommit;

        PendingCommits.get(function(responseData) {
            $scope.commits = responseData.commits;
        });

        $scope.syncCommits = function() {
            $http({method: 'POST', url: 'rest/commits/sync'})
                .success(function(data) {
                    $scope.commits = data.commits;
                })
                .error(function() {
                    $location.path("/error500");
                });
        };

    });