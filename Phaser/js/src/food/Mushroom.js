Mushroom = function (game, player, context) {
    // call super class
    Food.call(this, game, player, context);
};

// inherit from Food
Mushroom.prototype = Object.create(Food.prototype);
Mushroom.prototype.constructor = Mushroom;

Mushroom.prototype.effect = function () {
    this.kill();
	if(this.player.scale.y == 1) {
		var delayTime = 5;  //5s
   		var effectTime = 0.5; //  second
    	var increment = 0.1; // second
    	this.game.time.events.repeat(Phaser.Timer.SECOND * increment, effectTime/increment, this.large, this);
    	this.game.time.events.add(Phaser.Timer.SECOND * delayTime, this.afterEffect, this);
		this.player.score += this.context.score;
	}
};

Mushroom.prototype.large = function() {
	this.player.scale.x += 0.1;
	this.player.scale.y += 0.1;
};

Mushroom.prototype.afterEffect = function() {
	var effectTime = 0.5;
	var increment = 0.1;
    this.game.time.events.repeat(Phaser.Timer.SECOND * increment, effectTime/increment, this.normal, this);
	//this.appear();
};

Mushroom.prototype.normal = function() {
	this.player.scale.x -= 0.1;
	this.player.scale.y -= 0.1;
}
