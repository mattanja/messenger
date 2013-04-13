
angular.module('messengerApp.controllers', [])
	.controller('ListController', function($scope, $http) {

		// Init the objects used in this controller
		$scope.currentlist = { email: "", members: [], };
		$scope.newMemberemail = '';

		// Async get of list data
		$http.get('#').then(function(res) {
			// TODO: error handling
			$scope.currentlist = res.data;
		})

		// Remove member from list
		$scope.removeMember = function(removeEmail) {
			var data = { email: $scope.currentlist.email, addMembers: [], removeMembers: [{email: removeEmail, name: '', password: ''}]};
			// TODO: make route configurable in the template
			$http.post('/list/update/'+$scope.currentlist.email, data).then(function(res) {
				// TODO: error handling
				$scope.currentlist = res.data;
			});
		}

		$scope.addMember = function() {
			var data = { email: $scope.currentlist.email, addMembers: [{email: $scope.newMemberemail, name: '', password: ''}], removeMembers: []};
			// TODO: make route configurable in the template
			$http.post('/list/update/'+$scope.currentlist.email, data).then(function(res) {
				// TODO: error handling
				$scope.currentlist = res.data;
				$scope.newMemberemail = '';
			});
		}
	});
