Poison = function (game, player, context) {
    // call super class
    Food.call(this, game, player, context);
};

// inherit from Food
Poison.prototype = Object.create(Food.prototype);
Poison.prototype.constructor = Poison;
