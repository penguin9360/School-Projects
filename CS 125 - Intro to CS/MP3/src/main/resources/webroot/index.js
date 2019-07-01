/*
 * Game board functions.
 */

/*
 * Draw the initial game board. Much easier than cutting and pasting all of this
 * markup into the HTML file. Also shows how JS can modify the DOM.
 */
function drawBoard() {
  for (var y = 7; y >= 0; y--) {
    var currentRow = $(`<div class="row"></div>`);
    $("#board").append(currentRow);
    currentRow.append(`<div class="col-2"></div>`);
    for (var x = 0; x < 8; x++) {
      var currentCell = $(`<div class="col-1 square cell" ondrop="dropTile(event)" ondragover="allowDrop(event)"></div>`);
      currentCell.attr("data-x", x);
      currentCell.attr("data-y", y);
      currentRow.append(currentCell);
    }
    currentRow.append(`<div class="col-2"></div>`);
  }
  resizeSquares();
}

/*
 * Keep squares square when the window is resized.
 */
function resizeSquares() {
  $(".square").each(function() {
    $(this).height($(this).width());
  });
}

/* Board ID for backend calls. */
var boardID;

/* Board state returned by backend. */
var board;

/*
 * Create a new board. Called on startup and when the game ends.
 */
function newBoard() {
  return $.post("newBoard", JSON.stringify({
    width: 8,
    height: 8,
    n: 4
  })).done(function (data) {
    boardID = JSON.parse(data.boardID);
    board = JSON.parse(data.board);
    $(".tile").attr("draggable", "true");
    $("#reset").css("visibility", "hidden");
    message("Drag an animal to the bottom of the board to start...", "success");
    updateBoard();
  });
}

function updateBoard() {
  var sawAny = false;
  for (var x = 0; x < board.length; x++) {
    for (var y = 0; y < board[0].length; y++) {
      var square = $(`div[data-x=${x}][data-y=${y}]`);
      if (board[x][y] === null) {
        $(square).css("background-image", "");
      } else {
        sawAny = true;
        var image = imagesByID[board[x][y].id];
        $(square).css("background-image", `url("${image}")`);
      }
    }
  }
  if (sawAny) {
    message("");
  }
}

/*
 * Tile movement functions.
 */

/*
 * Allow dropping in board squares.
 */
function allowDrop(ev) {
  ev.preventDefault();
}

/*
 * Drag the game tile to a new location.
 */
function dragTile(ev) {
  var whichPlayer = $(event.target).data("player");
  /*
   * Save the serialized player information in a data transfer pipe for
   * retrieval by drop.
   */
  ev.dataTransfer.setData("player", JSON.stringify(players[whichPlayer]));
}

/*
 * Run on drop. Grabs the player information and communicates with the backend.
 */
function dropTile(ev) {
  ev.preventDefault();
  /*
   * Grab the serialized player information that was stored by dragTile.
   */
  var player = JSON.parse(ev.dataTransfer.getData("player"));
  var target = $(event.target);
  $.post("setBoardAt", JSON.stringify({
    player: player,
    board: boardID,
    x: target.data("x"),
    y: target.data("y")
  })).done(function (data) {
    var success = JSON.parse(data.success);
    if (success) {
      board = JSON.parse(data.board);
      updateBoard();
      var winner = JSON.parse(data.winner);
      if (winner != null) {
        playersByID[winner.id].score = winner.score;
        message(`${winner.name} won the game!`, "success");
        $(".tile").attr("draggable", "false");
        var selector = selectorsByPlayerID[winner.id];
        $(`#${selector}`).text(winner.score);
        $("#reset").css("visibility", "visible");
      }
    } else {
      message("That's not a valid move!", "danger");
    }
  });
}

/*
 * Player state maintenance. Most of this is done on the frontend.
 */

/*
 * Maintain player state. The web frontend implements a two-player game with no
 * rules about turns. Let the best animal win!
 */
var players = {};
var selectorsByPlayerID = {};
var playersByID = {};
var imagesByID = {};

/*
 * Set up initial players on restart.
 */
function newPlayer(whichPlayer, name) {
  return $.post("newPlayer", JSON.stringify({
    name: name
  })).done(function (data) {
    var player = JSON.parse(data.player);
    players[whichPlayer] = player;
    selectorsByPlayerID[player.id] = whichPlayer;
    playersByID[player.id] = player;
    if (name === "Chuchu") {
      imagesByID[player.id] = "img/Chuchu.png";
    } else if (name === "Xyz") {
      imagesByID[player.id] = "img/Xyz.png";
    }
  });
}

// From https://davidwalsh.name/javascript-debounce-function
function debounce(func, wait, immediate) {
  var timeout;
  return function() {
    var context = this, args = arguments;
    var later = function() {
      timeout = null;
      if (!immediate) func.apply(context, args);
    };
    var callNow = immediate && !timeout;
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
    if (callNow) func.apply(context, args);
  };
};

/*
 * Change a player's name.
 */
var changePlayerName = debounce(function (whichPlayer, name) {
  var player = players[whichPlayer];
  if (player.name === name) {
    return;
  }
  return $.post("changePlayerName", JSON.stringify({
    player: player,
    name: name
  })).done(function (data) {
    players[whichPlayer].name = JSON.parse(data.player).name;
  });
}, 1000);

/*
 * Miscellaneous helper functions.
 */

/*
 * Display a message in the board message location.
 */
function message(content, type) {
  $("#message").removeClass();
  if (content !== "") {
    $("#message").addClass("alert");
    $("#message").addClass("small");
    $("#message").addClass("alert-" + type);
  }
  $("#message").text(content);
}

/*
 * Set various things up on page load.
 */
$(function () {
  /*
   * Create the board and make squares square.
   */
  drawBoard();
  $(window).resize(resizeSquares);

  /*
   * Set click handlers.
   */
  $(".changePlayerName").on("input", function () {
    changePlayerName($(this).data("player"), $(this).val());
  });
  $("#reset").click(function () {
    newBoard();
  });

  /*
   * Set up the new board and create our two animal players.
   */
  newBoard()
    .then(newPlayer("firstPlayer", "Chuchu"))
    .then(newPlayer("secondPlayer", "Xyz"));
});


// vim: ts=2:sw=2:et:ft=javascript
