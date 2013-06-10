angular.module('messengerApp.controllers', [])

// NotificationService
.factory('notify', ['$window', function(win) {

    return {
		error: function(msg) {
			toastr.error(msg);
		},

		warning: function(msg) {
			toastr.warning(msg);
		},

		info: function(msg) {
			toastr.info(msg);
		},
    };
}])

// ListController
.controller('ListController', function($scope, $http, $log, notify) {

	// Init the objects used in this controller
	$scope.currentlist = { email: "", members: [], };
	$scope.newMemberemail = "test";
	$scope.messages = [ { key: "status", message: "Loading...", messageType: "info", }, ];

	// Async get of list data
	$http.get('#').then(function(res) {
		// Success
		$scope.currentlist = res.data;
		$scope.messages = [];

		$log.info("Data loaded.");
		notify.info("List data loaded...");
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
			$scope.messages = [];
		}, function(response) {
			// Error
			notify.error("Error removing member");
		});
	}

	$scope.addMember = function() {
		var data = { email: $scope.currentlist.email, addMembers: [$scope.newMemberemail], removeMembers: []};
		// TODO: make route configurable in the template
		$http.post('/list/update/' + $scope.currentlist.email, data).then(function(response) {
			// Success
			$scope.currentlist = response.data;
			$scope.messages = [];
		}, function(response) {
			notify.error("Error adding member");
		});
	}
})

// Not needed now, only with minification
.$inject = ['$scope', '$http', '$log', 'notify'];
