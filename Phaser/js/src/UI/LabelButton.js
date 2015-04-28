LabelButton = function(game, x, y, key, label, callback, callbackContext, overFrame, outFrame, downFrame, upFrame)
{
    Phaser.Button.call(this, game, x, y, key, callback, callbackContext, overFrame, outFrame, downFrame, upFrame);
    this.anchor.setTo(0.5, 0);
    //Style how you wish...
    this.style = {
        'font': '32px Impact',
        'fill': 'black'
    };   
    this.label = new Phaser.Text(game, 0, 0, "Label", this.style);
    this.addChild(this.label);
    this.setLabel(label);
    
    
    game.add.existing(this);
};

LabelButton.prototype = Object.create(Phaser.Button.prototype);
LabelButton.prototype.constructor = LabelButton;

LabelButton.prototype.setLabel = function(label)
{
    this.label.setText(label)
    this.label.x = (0 - this.label.width)/2;
    this.label.y = (this.height - this.label.height)/2;
};
