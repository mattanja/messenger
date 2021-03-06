angular.module('messengerApp', ['ui.bootstrap'])

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

// MailinglistController
.controller('MailinglistController', function($scope, $http, notify) {

	$scope.init = function() {
		$scope.lists = [];
		$scope.newMailinglist = { id: {value:0}, email: "", name: "", members: [], };

		// Load the data
		$http.get('/data/list/list').then(function(response) {
			$scope.lists = response.data;
			notify.info("Lists data loaded.");
		}, function(response) {
			$scope.lists = [];
			notify.error("Error loading lists data.");
		});
	}

	$scope.deleteList = function(listemail) {
		$http.post('/data/list/delete/' + listemail).then(function(response) {
			$scope.lists.splice($scope.lists.indexOf(listemail), 1);
			notify.info("Lists data loaded.");
		}, function(response) {
			notify.error("Error loading lists data.");
		});
	}

	$scope.addNewMailinglist = function() {
		var data = $scope.newMailinglist;
		$http.post('/data/list/create', data).then(function(response) {
			// Success
			var responseData = response.data;
			$scope.lists.push(responseData.email);
			$scope.newMailinglist = { id: {value:0}, email: "", name: "", members: [], };
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
})

// MailinglistDetailController
.controller('MailinglistDetailController', function($scope, $http, notify) {

	$scope.init = function(listid) {
		// Init the objects used in this controller
		$scope.currentlist = { email: '', members: [], };
		$scope.newMemberemail = '';

		// Async get of list data
		$http.get('/data/list/detail/' + listid).then(function(res) {
			// Success
			$scope.currentlist = res.data;

			notify.info("List data loaded...");
		}, function(response) {
			// Error
			$scope.currentlist = { email: "ERROR", members: [], };
		});
	}

	// Typeahead for the textbox adding new users to the list.
	$scope.getUserTypeahead = function(typed) {
		if (typed.length < 3) {
			return [];
		}

		return $http.post('/data/user/getUserTypeahead', { typeahead: typed }).then(function(response) {
			return response.data;
		})
	}

	// Remove member from list
	$scope.removeMember = function(removeEmail) {
		var data = { email: $scope.currentlist.email, addMembers: [], removeMembers: [removeEmail]};
		// TODO: make route configurable in the template
		$http.post('/data/list/update/' + $scope.currentlist.email, data).then(function(response) {
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
		$http.post('/data/list/update/' + $scope.currentlist.email, data).then(function(response) {
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
	$scope.newUser = { id: {value:0}, email: "", name: "", password: "" };

	// Async get of user data
	$http.get('/data/user/list').then(function(res) {
		// Success
		$scope.users = res.data;

		notify.info("User data loaded...");
	}, function(response) {
		// Error
		$scope.users = [];
	});

	$scope.addNewUser = function() {
		var data = $scope.newUser;
		$http.post('/data/user/create', data).then(function(response) {
			// Success
			var responseData = response.data;
			$scope.users.push(responseData);
			$scope.newUser = { id: {value:0}, email: "", name: "", password: "" };
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

	$scope.deleteUser = function(user) {
		var data = {};
		$http.post('/data/user/delete/' + user.id.value, data).then(function(response) {
			// Success
			var responseData = response.data;

			$scope.users.splice( $scope.users.indexOf(user), 1);
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
