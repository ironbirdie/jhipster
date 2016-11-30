(function() {
    'use strict';

    angular
        .module('jhipsterstoreApp')
        .controller('CategoryDetailController', CategoryDetailController);

    CategoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Category', 'SubCategory'];

    function CategoryDetailController($scope, $rootScope, $stateParams, previousState, entity, Category, SubCategory) {
        var vm = this;

        vm.category = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jhipsterstoreApp:categoryUpdate', function(event, result) {
            vm.category = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
