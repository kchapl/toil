Array.from(document.getElementsByTagName("canvas")).forEach(function (elt) {
  chart(elt);
});

function chart(canvas) {

  var dataset = canvas.dataset,
    labels = JSON.parse(dataset.labels),
    surplus = JSON.parse(dataset.surplus),
    income = JSON.parse(dataset.income),
    spend = JSON.parse(dataset.spend);

  var backgroundColour = surplus.map(function (s) {
    if (s > 0) {
      return '#32cd32';
    }
    else {
      return '#ff4500';
    }
  });

  var borderColour = surplus.map(function (s) {
    if (s > 0) {
      return '#006400';
    }
    else {
      return '#8b0000';
    }
  });

  return new Chart(canvas, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [
        {
          type: 'line',
          data: surplus,
          fill: false,
          borderColor: '#000000',
          borderWidth: 4
        },
        {
          data: income,
          backgroundColor: backgroundColour,
          borderColor: borderColour,
          borderWidth: 3
        },
        {
          data: spend,
          backgroundColor: backgroundColour,
          borderColor: borderColour,
          borderWidth: 3
        }
      ]
    },
    options: {
      title: {
        display: false
      },
      legend: {
        display: false
      },
      scales: {
        xAxes: [{
          stacked: true
        }],
        yAxes: [{
          ticks: {
            beginAtZero: true
          },
          scaleLabel: {
            display: true,
            labelString: 'pounds'
          }
        }]
      }
    }
  });
}
