Battery = function (game, player, context) {
    // call super class
    Food.call(this, game, player, context);
};

// inherit from Food
Battery.prototype = Object.create(Food.prototype);
Battery.prototype.constructor = Battery;

Battery.prototype.appear = function () {
	this.reset(this.game.world.randomX, this.context.position.y);
	if(this.x > this.game.world.width/2)
		this.body.velocity.setTo(-this.context.velocity.x,this.context.velocity.y);
	else
		this.body.velocity.setTo(this.context.velocity.x,this.context.velocity.y);
	this.body.gravity.setTo(this.context.gravity.x,this.context.gravity.y);
};

Battery.prototype.effect = function () {
	var delayTime = 5;
    var increment = 0.1; // second
    this.kill();
    this.game.time.events.repeat(Phaser.Timer.SECOND * increment, delayTime/increment, this.batteryEffect, this);
    //this.game.time.events.add(Phaser.Timer.SECOND * delayTime, this.afterEffect, this);
	this.player.score += this.context.score;
};

Battery.prototype.batteryEffect = function() {
	this.player.fat += this.context.fat;
	this.player.muscle += this.context.muscle;
	this.player.energy += this.context.energy;
};

Battery.prototype.afterEffect = function() {
	this.appear();
};
