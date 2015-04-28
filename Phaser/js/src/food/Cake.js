Cake = function (game, player, context) {
    // call super class
    Food.call(this, game, player, context);
};

// inherit from Food
Cake.prototype = Object.create(Food.prototype);
Cake.prototype.constructor = Cake;

// override method
/*Cake.prototype.effect = function (){
    this.player.updateScore(this.point);
    this.player.updateEnergy(100);
    this.player.updateFat(1000);
    this.player.updateMuscle(0);
    this.player.speed -= 100;
};*/