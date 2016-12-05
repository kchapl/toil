var ctx = document.getElementById("chart");

new Chart(ctx, {
    type: 'bar',
    data: chartData,
    options: {
        scales: {
            xAxes: [{
                stacked: true
            }],
            yAxes: [{
                ticks: {
                    beginAtZero: true
                }
            }]
        }
    }
});
