(function() {
    'use strict';

    angular
        .module('jhipsterstoreApp')
        .controller('ProductDetailController', ProductDetailController);

    ProductDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Product', 'SubCategory', 'Brand'];

    function ProductDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Product, SubCategory, Brand) {
        var vm = this;

        vm.product = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('jhipsterstoreApp:productUpdate', function(event, result) {
            vm.product = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
