Spice = function (game, player, context) {
    // call super class
    Food.call(this, game, player, context);
};

// inherit from Food
Spice.prototype = Object.create(Food.prototype);
Spice.prototype.constructor = Spice;

// override method
Spice.prototype.appear = function () {
    this.reset(this.game.world.randomX, this.context.position.y);
    this.body.velocity.setTo(this.context.velocity.x,this.context.velocity.y);
    this.body.gravity.setTo(this.context.gravity.x,this.context.gravity.y);
};

// override method
Spice.prototype.effect = function () {
    var delayTime = 5; //  second
    var increment = 0.5; // second
    this.kill();
    this.game.time.events.repeat(Phaser.Timer.SECOND * increment, delayTime/increment, this.spiceEffect, this);
    //this.game.time.events.add(Phaser.Timer.SECOND * delayTime, this.afterEffect, this);
    this.player.score += this.context.score; 
};

Spice.prototype.spiceEffect = function () {   
    this.player.fat += this.context.fat;    
};

Spice.prototype.afterEffect = function () {   
    this.appear();   
};

// for debug timmer event purpose
Spice.prototype.render = function () {

    //this.game.debug.text("Time until event: " + this.game.time.events.duration, 320, 32);

}
