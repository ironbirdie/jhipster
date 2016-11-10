(function() {
    'use strict';

    angular
        .module('jhipsterstoreApp')
        .controller('WishListDetailController', WishListDetailController);

    WishListDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'WishList', 'User'];

    function WishListDetailController($scope, $rootScope, $stateParams, previousState, entity, WishList, User) {
        var vm = this;

        vm.wishList = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jhipsterstoreApp:wishListUpdate', function(event, result) {
            vm.wishList = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
