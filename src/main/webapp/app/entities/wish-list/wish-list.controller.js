(function() {
    'use strict';

    angular
        .module('jhipsterstoreApp')
        .controller('WishListController', WishListController);

    WishListController.$inject = ['$scope', '$state', 'WishList'];

    function WishListController ($scope, $state, WishList) {
        var vm = this;
        
        vm.wishLists = [];

        loadAll();

        function loadAll() {
            WishList.query(function(result) {
                vm.wishLists = result;
            });
        }
    }
})();
