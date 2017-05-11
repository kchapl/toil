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
          data: balances
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
