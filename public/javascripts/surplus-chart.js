var ctx = document.getElementById("chart");

new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
        datasets: [incomeDataset, spendDataset, surplusDataset]
    },
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
