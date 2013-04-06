
// First implementation and tests of angularjs methods

function ListController($scope) {
	$scope.members = [
		{ memberemail: 'test1@test.com' },
		{ memberemail: 'test2@test.com' },
		{ memberemail: 'test3@test.com' },
	];

	$scope.removeMember = function(removeEmail) {
		var currentMembers = $scope.members;
		angular.forEach(currentMembers, function(item) {
			if (item.memberemail != removeEmail) {
				$scope.members.push(item);
			}
		})
	}

	$scope.addMember = function() {
		$scope.members.push({ memberemail: $scope.newMemberemail});
		$scope.newMemberemail = '';
	}
}
