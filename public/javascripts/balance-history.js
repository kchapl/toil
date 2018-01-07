Array.from(document.getElementsByTagName("canvas")).forEach(function (elt) {
  chart(elt);
});

function chart(canvas) {

  var dataset = canvas.dataset,
    labels = JSON.parse(dataset.labels),
    balances = JSON.parse(dataset.balances);

  return new Chart(canvas, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [
        {
          data: balances,
          fill: false,
          borderColor: 'cornflowerblue',
          borderWidth: 2,
          radius: 1
        }
      ]
    },
    options: {
      title: {
        display: false
      },
      legend: {
        display: false
      }
    }
  });
}
