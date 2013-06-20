angular.module('messengerApp.controllers', [])

// NotificationService
.factory('notify', function() {

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
})

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
			var listUpdateResponse = response.data;
			if (listUpdateResponse) {
				notify.error("Error removing member " + removeEmail + ": " + listUpdateResponse.messages.join("\n<br />"));
			} else {
				// Response can not be processed
				notify.error("Error removing member");
			}
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
			// Error
			var listUpdateResponse = response.data;
			if (listUpdateResponse) {
				notify.error("Error adding member " + addEmail + ": " + listUpdateResponse.messages.join("\n<br />"));
			} else {
				notify.error("Error adding member " + addEmail);
			}
		});
	}
})

// UserController
.controller('UserController', function($scope, $http, notify) {
	// Initial data & members
	$scope.newUser = { email: "", name: "", password: "" };

	// Async get of user data
	$http.get('/users').then(function(res) {
		// Success
		$scope.users = res.data;

		notify.info("User data loaded...");
	}, function(response) {
		// Error
		$scope.users = [];
	});

	$scope.addNewUser = function() {
		var data = $scope.newUser;
		$http.post('/user/newUser', data).then(function(response) {
			// Success
			var responseData = response.data;
			$scope.newUser = { email: "", name: "", password: "" };
			notify.info("success response code");
		}, function(response) {
			// Error
			var responseData = response.data;
			if (responseData) {
				notify.error("2: Error adding new user.");
			} else {
				notify.error("3: Error adding new user.");
			}
		});
	}
	
	$scope.deleteUser = function(deleteUserEmail) {
		var data = {};
		$http.post('/user/delete/' + deleteUserEmail, data).then(function(response) {
			// Success
			var responseData = response.data;
			notify.info("user deleted");
		}, function(response) {
			// Error
			var responseData = response.data;
			if (responseData) {
				notify.error("2: Error deleting user.");
			} else {
				notify.error("3: Error deleting user.");
			}
		});
	}

	$scope.newUserGeneratePassword = function(event) {
		event.preventDefault();

		var length = 8,
        charset = "abcdefghijklnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
        retVal = "";
	    for (var i = 0, n = charset.length; i < length; ++i) {
	        retVal += charset.charAt(Math.floor(Math.random() * n));
	    }
		$scope.newUser.password = retVal;
		$scope.newUser.generatedPassword = $scope.newUser.password;
	}
})

// Not needed now, only with minification
.$inject = ['$scope', '$http', 'notify'];
