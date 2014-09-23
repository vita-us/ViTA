describe('CssClass module', function() {

  beforeEach(module('vitaServices'));

  it('should return a css class for a ranking value', inject(function(CssClass) {
    var rankingValue = 13;

    var rankingCssClass = CssClass.forRankingValue(rankingValue);

    expect(rankingCssClass).toEqual("ranking-" + rankingValue);
  }));

});
