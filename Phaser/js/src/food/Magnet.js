Magnet = function (game, player, context, food) {
    // call super class
    Food.call(this, game, player, context);
	this.food = food;
};

// inherit from Food
Magnet.prototype = Object.create(Food.prototype);
Magnet.prototype.constructor = Magnet;

Magnet.prototype.appear = function () {
	this.reset(this.game.world.randomX, this.context.position.y);
	if(this.x > this.game.world.width/2)
		this.body.velocity.setTo(-this.context.velocity.x,this.context.velocity.y);
	else
		this.body.velocity.setTo(this.context.velocity.x,this.context.velocity.y);
	this.body.gravity.setTo(this.context.gravity.x,this.context.gravity.y);
};

Magnet.prototype.effect = function () {
	var delayTime = 5;  //5s
    var increment = 0.1; // second
    this.kill();
    this.game.time.events.repeat(Phaser.Timer.SECOND * increment, delayTime/increment, this.magnetEffect, this);
    //this.game.time.events.add(Phaser.Timer.SECOND * delayTime, this.afterEffect, this);
	this.player.score += this.context.score;
};

Magnet.prototype.magnetEffect = function() {
	for(var i = 0; i < this.food.length; i++){
		if(this.food[i].context.name != 'Poison' && this.food[i].context.name != 'Emetics' && this.food[i].context.name != 'Magnet')
			this.game.physics.arcade.moveToObject(this.food[i], this.player, 500);
	}
};

Magnet.prototype.afterEffect = function() {
	this.appear();
};
