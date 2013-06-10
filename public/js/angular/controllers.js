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
.controller('ListController', function($scope, $http, notify) {

	// Init the objects used in this controller
	$scope.currentlist = { email: '', members: [], };
	$scope.newMemberemail = '';

	// Async get of list data
	$http.get('#').then(function(res) {
		// Success
		$scope.currentlist = res.data;

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
			var listUpdateResponse = response.data;
			if (listUpdateResponse.success) {
				$scope.currentlist = listUpdateResponse.mailinglist;
				notify.info("Successfully removed member " + removeEmail);
			} else {
				notify.error("Error removing member " + removeEmail + ": " + listUpdateResponse.messages.join("\n<br />"));
			}
		}, function(response) {
			// Error
			notify.error("Error removing member");
		});
	}

	$scope.addMember = function() {
		var addEmail = $scope.newMemberemail
		var data = { email: $scope.currentlist.email, addMembers: [addEmail], removeMembers: []};
		// TODO: make route configurable in the template
		$http.post('/list/update/' + $scope.currentlist.email, data).then(function(response) {
			var listUpdateResponse = response.data;
			if (listUpdateResponse.success) {
				$scope.currentlist = listUpdateResponse.mailinglist;
				notify.info("Successfully added member " + addEmail);
				$scope.newMemberemail = '';
			} else {
				notify.error("Error adding member " + addEmail + ": " + listUpdateResponse.messages.join("\n<br />"));
			}
		}, function(response) {
			notify.error("Error adding member " + addEmail);
		});
	}
})

// Not needed now, only with minification
.$inject = ['$scope', '$http', 'notify'];
