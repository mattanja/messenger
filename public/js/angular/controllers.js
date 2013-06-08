
angular.module('messengerApp.controllers', [])
	.controller('ListController', function($scope, $http) {

		// Init the objects used in this controller
		$scope.currentlist = { email: "", members: [], };
		$scope.newMemberemail = '';

		// Async get of list data
		$http.get('#').then(function(res) {
			// Success
			$scope.currentlist = res.data;
		}, function(response) {
			// Error
			$scope.currentlist = { email: "ERROR", members: [], };
		});

		// Remove member from list
		$scope.removeMember = function(removeEmail) {
			var data = { email: $scope.currentlist.email, addMembers: [], removeMembers: [removeEmail]};
			// TODO: make route configurable in the template
			$http.post('/list/update/' + $scope.currentlist.email, data).then(function(response) {
				// Success
				$scope.currentlist = response.data;
			}, function(response) {
				// Error
				//
				// TODO
				$scope.currentlist = { email: "ERROR", members: [], };
			});
		}

		$scope.addMember = function() {
			var data = { email: $scope.currentlist.email, addMembers: [$scope.newMemberemail], removeMembers: []};
			// TODO: make route configurable in the template
			$http.post('/list/update/' + $scope.currentlist.email, data).then(function(response) {
				// Success
				$scope.currentlist = response.data;
				$scope.newMemberemail = '';
			}, function(response) {
				// Error
				//
				// TODO
				$scope.currentlist = { email: "ERROR", members: [], };
			});
		}
	});
