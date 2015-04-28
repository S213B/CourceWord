Emetics = function (game, player, context) {
    // call super class
    Food.call(this, game, player, context);
	this.lifespan = 6000;
    this.body.collideWorldBounds = true;
	this.body.bounce.set(0.6);
};

// inherit from Food
Emetics.prototype = Object.create(Food.prototype);
Emetics.prototype.constructor = Emetics;

Emetics.prototype.appear = function () {
	this.reset(this.game.world.randomX, this.context.position.y);
	if(this.x > this.game.world.width/2)
		this.body.velocity.setTo(-this.context.velocity.x,this.context.velocity.y);
	else
		this.body.velocity.setTo(this.context.velocity.x,this.context.velocity.y);
	this.body.gravity.setTo(this.context.gravity.x,this.context.gravity.y);
	this.lifespan = 4000;
};

Emetics.prototype.effect = function () {
	var delayTime = 5;
    var increment = 0.1; // second
    this.kill();
    this.game.time.events.repeat(Phaser.Timer.SECOND * increment, delayTime/increment, this.emeticsEffect, this);
    //this.game.time.events.add(Phaser.Timer.SECOND * delayTime, this.afterEffect, this);
};

Emetics.prototype.emeticsEffect = function() {
	this.player.score += this.context.score;
	this.player.fat += this.context.fat;
	this.player.muscle += this.context.muscle;
	this.player.energy += this.context.energy;
};

Emetics.prototype.afterEffect = function() {
	this.appear();
};
