Reds = function (game, player, context) {
    // call super class
    Food.call(this, game, player, context);
};

// inherit from Food
Reds.prototype = Object.create(Food.prototype);
Reds.prototype.constructor = Reds;

// override method
/*Reds.prototype.effect = function (){
    this.player.updateScore(5000);
    this.player.updateEnergy(100);
    this.player.updateFat(1000);
    this.player.updateMuscle(0);
    //this.player.speed -= 100;
};*/

