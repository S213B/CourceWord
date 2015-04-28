Background = function (game) {
    this.game = game;
    this.ground;
    this.background;

    // Background
    this.background = game.add.tileSprite(0, 0, game.width, game.height, 'background');
      
    // Ground
    this.ground = game.add.tileSprite(0, 560, game.width, 40, 'ground');
    game.physics.enable(this.ground, Phaser.Physics.ARCADE);
    this.ground.body.immovable = true;
    this.ground.body.collideWorldBounds = true;
    
    // cloud
    this.cloud1 = game.add.tileSprite(game.world.randomX, 0, 400, 300, 'cloud1');
    game.physics.enable(this.cloud1, Phaser.Physics.ARCADE);
    this.cloud1.anchor.setTo(0, 0.5); 
    this.cloud2 = game.add.tileSprite(game.world.randomX, 150, 400, 200, 'cloud2');
    game.physics.enable(this.cloud2, Phaser.Physics.ARCADE);
    this.cloud2.anchor.setTo(0, 0.5); 
    

};

Background.prototype.constructor = Background;

Background.prototype.update = function (){
    this.cloud1.body.velocity.x = 50;
    this.screenWrap(this.cloud1);
    this.cloud2.body.velocity.x = 100;
    this.screenWrap(this.cloud2);
};

Background.prototype.screenWrap = function (sprite) {

    if (sprite.x < 0)
    {
        sprite.x = this.game.width;
    }
    else if (sprite.x > this.game.width + 200)
    {
        sprite.x = 0;
        sprite.anchor.setTo(1, 0.5);
    }

    if (sprite.y < 0)
    {
        sprite.y = this.game.height;
    }
    else if (sprite.y > this.game.height)
    {
        sprite.y = 0;
    }

}
